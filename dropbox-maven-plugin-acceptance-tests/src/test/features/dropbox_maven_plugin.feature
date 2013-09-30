#encoding: utf-8
Feature: Showcase the dropbox-maven-plugin integration
  In order to verify that the dropbox-maven-plugin works
  As someone who wants to interact with dropbox through maven 
  I should be able to run this scenario and see that the steps pass
 
  Background:
    Given a dropbox plugin 'timezra.maven:dropbox-maven-plugin:${project.version}'
    And a local repository '${local-repository}'
    And a client identifier '${client_identifier}'
    And an access token '${access_token}'
  
  Scenario: Gets Info
    When I ask for account information
    Then I should see a userId and displayName

  @creates_resource
  Scenario: Creates a Folder
    When I ask to create a folder with path '/subfolder'
    Then that folder should exist in dropbox
    
  Scenario: Gets Metadata
    When I ask to get metadata for '/'
    Then I should see Folder metadata
  
  @creates_resource
  Scenario: Uploads a File
    When I ask to upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    Then that file should exist in dropbox
    
    
    