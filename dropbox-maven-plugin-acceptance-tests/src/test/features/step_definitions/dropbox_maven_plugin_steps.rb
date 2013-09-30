#encoding: utf-8

# require 'java'
# require 'test/unit'

# require 'rubygems'
# require 'rspec'

require 'cucumber/api/jruby/en'
require 'rspec/expectations'

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
  @output.should =~ /userId/
  @output.should =~ /displayName/
end

When /^I ask to create a folder with path (.*)$/ do |path|
  @path = path
  `mvn -Dmaven.repo.local=#{@repo} #{@plugin}:create_folder -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token} -Dpath=#{@path}`
end

Then /^that folder should exist$/ do
  @output = `mvn -Dmaven.repo.local=#{@repo} #{@plugin}:metadata -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token} -Dpath=#{@path}`
  @output.should =~ /Folder\(\"#{@path}\"/
end

When /^I ask to get metadata for (.*)$/ do |path|
  @path = path
  @output = `mvn -Dmaven.repo.local=#{@repo} #{@plugin}:metadata -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token} -Dpath=#{@path}`
end

Then /^Then I should see (.*) metadata$/ do |resourceType|
  @output.should =~ /#{resourceType}\(\"#{@path}\"/
end

After('@creates_resource') do |s|
  `mvn -Dmaven.repo.local=#{@repo} #{@plugin}:delete -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token} -Dpath=#{@path}`
end
