#encoding: utf-8

require "open-uri"
require 'cucumber/api/jruby/en'
require 'rspec/expectations'
World(RSpec::Matchers)

Given /^a dropbox plugin '(.*)'$/ do |plugin|
  @plugin = plugin
end

And /^a local repository '(.*)'$/ do |repo|
  @repo = repo
end

And /^a client identifier '(.*)'$/ do |client_identifier|
  @client_identifier = client_identifier
end

And /^an access token '(.*)'$/ do |access_token|
  @access_token = access_token
end

When /^I ask for account information$/ do
  @output = mvn('info', {})
end

Then /^I should see a userId and displayName$/ do
  @output.should match(/userId/)
  @output.should match(/displayName/)
end

When /^I create a folder with path '(.*)'$/ do |path|
  @path = path
  mvn('create_folder', { 'path' => @path })
end

Then /^(?:the|that) (?:original )?(file|folder) (should|should not) be in dropbox$/ do |resourceType, should_or_should_not|
  @output = get_metadata_from_dropbox @path
  @output.send _(should_or_should_not), match(/^\[INFO\] #{resourceType.capitalize}\("#{@path}"/)
end

Then /^that (?:copy|moved file) should be in dropbox$/ do
  @output = get_metadata_from_dropbox @to_path
  @output.should match(/^\[INFO\] File\("#{@to_path}"/)
end

When /^I get metadata for '(.*)'$/ do |path|
  @path = path
  @output = get_metadata_from_dropbox @path
end

Then /^I should see (?:the )?(file|folder) metadata$/ do |resourceType|
  @output.should match(/^\[INFO\] #{resourceType.capitalize}\("#{@path}"/)
end

When /^I upload the file '(.*)' to '(.*)'$/ do |file, path|
  @file = file
  @path = path
  @output = mvn('files_put', { 'file' => @file, 'path' => @path })
end

And /^I download the file to '(.*)'$/ do |file|
  @file = file
  @output = mvn('files', { 'file' => @file, 'path' => @path })
end

Then /^that file should exist in the local file system$/ do
  File.exist?(@file).should be_true 
end

And /^I delete that file from dropbox$/ do
  delete_from_dropbox @path
end

And /^I get the delta$/ do
  @output = mvn('delta', { 'access_token' => @access_token })
  @cursor = @output.match(/^\[INFO\] cursor="(.*)"$/)[1]
end

And /^I get the delta again$/ do
  @output = mvn('delta', { 'cursor' => @cursor })
end

Then /^I should see that the file has been deleted$/ do
  @output.should match(/^\[INFO\] \(lcPath="#{@path}", metadata=null\)$/)
end

And /^I get revisions for the file$/ do
  @output = get_revisions_from_dropbox @path
end

Then /^I should see its revisions$/ do
  @output.should match(/^\[INFO\] File\("#{@path}"(?:.*)rev="(\w+)"\)$/)
end

And /^I restore that file's previous revision$/ do
  @output = get_revisions_from_dropbox @path
  rev = @output.scan(/^\[INFO\] File\("#{@path}"(?:.*)rev="(\w+)"\)$/)[1][0]
  mvn('restore', { 'path' => @path, 'rev' => rev })
end

And /^I search for '(.*)' in '(.*)'$/ do |query, search_path|
  @output = mvn('search', { 'query' => query, 'path' => search_path })
end

And /^I share it$/ do
  @output = mvn('shares', { 'path' => @path })
  @share = @output.match(/^\[INFO\] (https:\/\/db\.tt\/\w+)$/)[1]
end

Then /^I should see a file preview$/ do
  open(@share).status[0].should == "200"
end

And /^I stream it$/ do
  @output = mvn('media', { 'path' => @path })
  @media = @output.match(/^\[INFO\] url=(https:\/\/dl\.dropboxusercontent\.com\/.+)$/)[1]
end

Then /^I should get the file contents$/ do
  open(@media).read.should == IO.read(@file)
end

And /^I get a copy reference for it$/ do
  @output = mvn('copy_ref', { 'path' => @path })
  @copy_ref = @output.match(/^\[INFO\] copy_ref=(\w+)$/)[1]
end

And /^I copy the reference to '(.*)'$/ do |to_path|
  @to_path = to_path
  mvn('copy', { 'from_copy_ref' => @copy_ref, 'to_path' => @to_path })
end

And /^I copy it to '(.*)'$/ do |to_path|
  @to_path = to_path
  mvn('copy', { 'from_path' => @path, 'to_path' => @to_path })
end

And /^I move it to '(.*)'$/ do |to_path|
  @to_path = to_path
  mvn('move', { 'from_path' => @path, 'to_path' => @to_path })
end

And /^I ask for a (.*) thumbnail at '(.*)'$/ do |format, file|
  @file = file
  mvn('thumbnails', { 'path' => @path, 'file' => @file, 'format' => format })
end

When /^I upload the first (\d+) bytes of the file '(.*)'$/ do |chunk_size, file|
  @file = file
  @offset = chunk_size
  @output = mvn('chunked_upload', { 'file' => @file, 'chunkSize' => chunk_size })
  @upload_id = @output.match(/^\[INFO\] upload_id=(.*)$/)[1]
end

And /^upload the rest of the file$/ do
  mvn('chunked_upload', { 'file' => @file, 'upload_id' => @upload_id, 'offset' => @offset })
end

And /^commit the upload to '(.*)'$/ do |path|
  @path = path
  mvn('commit_chunked_upload', { 'path' => @path, 'upload_id' => @upload_id })
end

After('@creates_dropbox_resource') do |s|
  delete_from_dropbox @path
end

After('@creates_copied_dropbox_resource') do |s|
  delete_from_dropbox @to_path
end

After('@creates_local_resource') do |s|
  File.delete(@file)
end

def _ (words)
  words.gsub(' ', '_').to_sym
end

def get_revisions_from_dropbox (resource)
  mvn('revisions', { 'path' => resource })
end

def get_metadata_from_dropbox (resource)
  mvn('metadata', { 'path' => resource })
end

def delete_from_dropbox (resource)
  mvn('delete', { 'path' => resource })
end

def mvn (goal, params)
  params['maven.repo.local'] = @repo
  params['clientIdentifier'] = @client_identifier
  params['accessToken'] = @access_token
  command = params.reduce("mvn -N -B #{@plugin}:#{goal}") {|s, (k, v)|
    s + " -D#{k}=\"#{v}\""
  }
  `#{command}`
end


# ##############################################################################
# Copyright (c) 2013 timezra
# 
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
# 
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
# 
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
# ##############################################################################
