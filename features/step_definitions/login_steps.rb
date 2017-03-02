Given  /^I login RapidReg with "(.*?)" account$/ do |account|
  p account
  login_page.login(test_account[account])
end

When /^I login RapidReg for the first time with incorrect "(.*?)" or "(.*?)" and correct "(.*?)"$/ do |username,password,url|
  login_page.loginAs(username,password,url)
end

When /^I login RapidReg for the first time with "(.*?)" and "(.*?)" and incorrect "(.*?)"$/ do |username,password,url|
  login_page.loginAs(username,password,url)
end

When /^I login RapidReg for the first time with "(.*?)" and "(.*?)" and "(.*?)"$/ do |username,password,url|
  login_page.loginAs(username,password,url)
end

When /^I re-login RapidReg with incorrect "(.*?)" or "(.*?)" and correct "(.*?)"$/ do |username,password,url|
  login_page.reLoginAs(username,password,url)
end

When /^I re-login RapidReg with "(.*?)" and "(.*?)" and incorrect "(.*?)"$/ do |username,password,url|
  login_page.reLoginAs(username,password,url)
end

When /^I re-login RapidReg with "(.*?)" and "(.*?)" and "(.*?)"$/ do |username,password,url|
  login_page.reLoginAs(username,password,url)
end

When /^I press "(.*?)" button$/ do |button|
   base_page.clickById(button)
   if button == "add"
     page_title = main_menu.getPageTitle
     until page_title.include?("New") do
       puts "Syncing forms, please wait..."
       sleep 5
       base_page.clickById(button)
     end
   end
 end

 Then /^I should see "(.*?)"$/ do |text|
   base_page.verifyPromptExist(text)
 end

When /^I logout$/ do
  login_page.logout
end

And /^I should see current user is "(.*?)"$/ do |username|
  sleep 5 #must
  actual_user = login_page.getCurrentUser
  raise ("NOT right #{username} for #{actual_user}") unless username == actual_user
end

And /^the organization is "(.*?)"$/ do |org|
  actual_org = login_page.getUserOrganization
  raise ("NOT right #{org} for #{actual_org}") unless org == actual_org
end
