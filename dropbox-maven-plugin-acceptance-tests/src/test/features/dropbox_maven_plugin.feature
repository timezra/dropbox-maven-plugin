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
    Then that folder should be in dropbox
    
  Scenario: Gets Metadata
    When I get metadata for '/'
    Then I should see folder metadata
  
  @creates_dropbox_resource
  Scenario: Uploads a New File
    When I upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    Then that file should be in dropbox
    
  @creates_dropbox_resource
  @creates_local_resource
  Scenario: Gets a File
    When I upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    And I download the file to '${project.build.directory}/my_testfile.txt'
    Then that file should exist in the local file system
  
  Scenario: Deletes a File
    When I upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    And I delete that file from dropbox
    Then that file should not be in dropbox
    
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
    Then that file should be in dropbox
    
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
    
  @creates_dropbox_resource
  @creates_copied_dropbox_resource
  Scenario: Copies From a Reference
    When I upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    And I get a copy reference for it
    And I copy the reference to '/copied_testfile.txt'
    Then that copy should be in dropbox
     
  @creates_dropbox_resource
  @creates_copied_dropbox_resource
  Scenario: Copies From a File
    When I upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    And I copy it to '/copied_testfile.txt'
    Then that copy should be in dropbox
    
  @creates_copied_dropbox_resource
  Scenario: Moves a File
    When I upload the file '${project.build.testOutputDirectory}/testfile.txt' to '/testfile.txt'
    And I move it to '/moved_testfile.txt'
    Then that moved file should be in dropbox
    And the original file should not be in dropbox
    
  @creates_dropbox_resource
  @creates_local_resource
  Scenario: Gets a Thumbnail
    When I upload the file '${project.build.testOutputDirectory}/testimage.png' to '/testimage.png'
    And I ask for a png thumbnail at '${project.build.directory}/my_thumbnail.png'
    Then that file should exist in the local file system
    
  @creates_dropbox_resource
  Scenario: Uploads a File In Multiple Chunks
    When I upload the first 1000 bytes of the file '${project.build.testOutputDirectory}/testimage.png'
    And upload the rest of the file to '/testfile.txt'
    Then that file should be in dropbox
    
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