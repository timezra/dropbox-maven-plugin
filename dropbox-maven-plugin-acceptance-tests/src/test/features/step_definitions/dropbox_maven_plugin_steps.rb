#encoding: utf-8

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
  @output = `mvn -Dmaven.repo.local=#{@repo} #{@plugin}:info -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token}`
end

Then /^I should see a userId and displayName$/ do
  @output.should match(/userId/)
  @output.should match(/displayName/)
end

When /^I create a folder with path '(.*)'$/ do |path|
  @path = path
  `mvn -Dmaven.repo.local=#{@repo} #{@plugin}:create_folder -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token} -Dpath=#{@path}`
end

Then /^that (\w+) (should|should not) exist in dropbox$/ do |resourceType, should_or_should_not|
  @output = get_metadata_from_dropbox @path
  @output.send _(should_or_should_not), match(/^\[INFO\] #{resourceType.capitalize}\("#{@path}"/)
end

When /^I get metadata for '(.*)'$/ do |path|
  @path = path
  @output = get_metadata_from_dropbox @path
end

Then /^I should see (?:the )?(\w+) metadata$/ do |resourceType|
  @output.should match(/^\[INFO\] #{resourceType.capitalize}\("#{@path}"/)
end

When /^I upload the file '(.*)' to '(.*)'$/ do |file, path|
  @path = path
  @output = `mvn -Dmaven.repo.local=#{@repo} #{@plugin}:files_put -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token} -Dfile=#{file} -Dpath=#{@path}`
end

And /^I download the file to '(.*)'$/ do |file|
  @file = file
  @output = `mvn -Dmaven.repo.local=#{@repo} #{@plugin}:files -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token} -Dfile=#{@file} -Dpath=#{@path}`
end

Then /^that file should exist in the local file system$/ do
  File.exist?(@file).should be_true 
end

And /^I delete that file from dropbox$/ do
  delete_from_dropbox @path
end

And /^I get the delta$/ do
  @output = `mvn -Dmaven.repo.local=#{@repo} #{@plugin}:delta -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token}`
  @cursor = @output.match(/^\[INFO\] cursor="(.*)"$/)[1]
end

And /^I get the delta again$/ do
  @output = `mvn -Dmaven.repo.local=#{@repo} #{@plugin}:delta -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token} -Dcursor="#{@cursor}"`
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
  `mvn -Dmaven.repo.local=#{@repo} #{@plugin}:restore -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token} -Dpath="#{@path}" -Drev=#{rev}`
end

And /^I search for '(.*)' in '(.*)'$/ do |query, search_path|
  @output = `mvn -Dmaven.repo.local=#{@repo} #{@plugin}:search -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token} -Dquery=#{query} -Dpath="#{search_path}"`
end

After('@creates_dropbox_resource') do |s|
  delete_from_dropbox @path
end

After('@creates_local_resource') do |s|
  File.delete(@file)
end

def _ (words)
  words.gsub(' ', '_').to_sym
end

def get_revisions_from_dropbox (resource)
  `mvn -Dmaven.repo.local=#{@repo} #{@plugin}:revisions -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token} -Dpath=#{resource}`
end

def get_metadata_from_dropbox (resource)
  `mvn -Dmaven.repo.local=#{@repo} #{@plugin}:metadata -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token} -Dpath=#{resource}`
end

def delete_from_dropbox (resource)
  `mvn -Dmaven.repo.local=#{@repo} #{@plugin}:delete -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token} -Dpath=#{resource}`
end
