Given /^I launch primero$/ do
  sleep 10
end

When /^I login RapidReg for the first time with "(.*?)" and "(.*?)" and "(.*?)"$/ do |username,password,url|
  login_page.loginAs(username,password,url)
end

When /^I re-login RapidReg with "(.*?)" and "(.*?)"$/ do |username,password|
  login_page.reLoginAs(username,password)
end

When /^I press "(.*?)"$/ do |button|
  base_page.clickById(button)
end

Then /^I should see "(.*?)"$/ do |text|
  base_page.verifyPromptExist(text)
end

When /^I logout$/ do
  login_page.logout
end

