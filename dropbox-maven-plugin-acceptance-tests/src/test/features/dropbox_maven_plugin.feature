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
  
  Scenario: Gets Account Info
    When I ask for account information
    Then I should see a userId and displayName

  @creates_dropbox_resource
  Scenario: Creates a New Folder
    When I create a folder with path '/subfolder'
    Then that folder should exist in dropbox
    
  Scenario: Gets Metadata
    When I get metadata for '/'
    Then I should see folder metadata
  
  @creates_dropbox_resource
  Scenario: Uploads a New File
    When I upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    Then that file should exist in dropbox
    
  @creates_dropbox_resource
  @creates_local_resource
  Scenario: Gets a File
    When I upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    And I download the file to '${project.build.directory}/my_testfile.txt'
    Then that file should exist in the local file system
  
  Scenario: Deletes a File
    When I upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    And I delete that file from dropbox
    Then that file should not exist in dropbox
    
  Scenario: Gets a Delta
    When I upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    And I get the delta
    And I delete that file from dropbox
    And I get the delta again
    Then I should see that the file has been deleted
    
  @creates_dropbox_resource
  Scenario: Gets Revisions
    When I upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    And I get revisions for the file
    Then I should see its revisions

  @creates_dropbox_resource
  Scenario: Restores a File
    When I upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    And I delete that file from dropbox
    And I restore that file's previous revision
    Then that file should exist in dropbox
    
  @creates_dropbox_resource
  Scenario: Searches For a File
    When I upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    And I search for 'test' in '/'
    Then I should see the file metadata
    
  @creates_dropbox_resource
  Scenario: Shares a File
    When I upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    And I share it
    Then I should see a file preview
    
  @creates_dropbox_resource
  Scenario: Streams a File
    When I upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    And I stream it
    Then I should get the file contents
    