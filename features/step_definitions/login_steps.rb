Given /^I login RapidReg with "(.*?)" account$/ do |account|
  login_page.login(test_account[account])
end

When /^I login RapidReg for the first time with "(.*?)" and "(.*?)" and "(.*?)"$/ do |username,password,url|
  login_page.loginAs(username,password,url)
end

When /^I re-login RapidReg with "(.*?)" and "(.*?)"$/ do |username,password|
  login_page.reLoginAs(username,password)
end

When /^I press "(.*?)" button$/ do |button|
  base_page.clickById(button)
  sleep 5       #must
  while base_page.ifTextExist("Tracing request forms were not pulled down successfully, press OK to resync.") do
    puts "Syncing forms, please try it later"
    base_page.clickByXpath("//android.widget.Button[@text='OK']")
    base_page.clickById("fab_expand_menu_button")
    base_page.clickById(button)
  end
end

Then /^I should see "(.*?)"$/ do |text|
  base_page.verifyPromptExist(text)
end

When /^I logout$/ do
  login_page.logout
end

And /^I should see current user is "(.*?)"$/ do |username|
  sleep 10   # must
  actual_user = login_page.getCurrentUser
  raise ("NOT right #{username} for #{actual_user}") unless username == actual_user
end

And /^the organization is "(.*?)"$/ do |org|
  actual_org = login_page.getUserOrganization
  raise ("NOT right #{org} for #{actual_org}") unless org == actual_org
end
