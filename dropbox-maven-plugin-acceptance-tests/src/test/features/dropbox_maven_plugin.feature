#encoding: utf-8
Feature: Showcase the dropbox-maven-plugin integration
  In order to verify that the dropbox-maven-plugin works
  As someone who wants to interact with dropbox through maven 
  I should be able to run this scenario and see that the steps pass
 
  Scenario: Dropbox Info
    Given a client identifier '${client_identifier}'
    And an access token '${access_token}'
    When I ask for account information
    Then I should see a userId and displayName
