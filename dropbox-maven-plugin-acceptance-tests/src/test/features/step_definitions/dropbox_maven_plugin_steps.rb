#encoding: utf-8

# require 'java'
# require 'test/unit'

# require 'rubygems'
# require 'rspec'

require 'cucumber/api/jruby/en'
require 'rspec/expectations'

Given /^a client identifier '(.*)'$/ do |client_identifier|
  @client_identifier = client_identifier
end

And /^an access token '(.*)'$/ do |access_token|
  @access_token = access_token
end

When /^I ask for account information$/ do 
  @output = `mvn -Dmaven.repo.local='${project.build.directory}/local-repo' timezra.maven:dropbox-maven-plugin:${project.version}:info -DclientIdentifier="#{@client_identifier}" -DaccessToken=#{@access_token}`
end

Then /^I should see a userId and displayName$/ do
  @output.should =~ /userId/
  @output.should =~ /displayName/
end
