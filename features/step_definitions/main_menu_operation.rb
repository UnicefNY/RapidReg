When /^I swipe to hide the main menu$/ do
  main_menu.hideMainMenu
end

When /^I swipe to show the main menu$/ do
  main_menu.showMainMenu
end

When /^I press menu tab "(.*?)"$/ do |tabname|
  main_menu.pressMenuTab(tabname)
end

When /^I press menu button$/ do
  main_menu.pressMenuButton
end

Then /^I should see page title is "(.*?)"$/ do |title|
  main_menu.getPageTitle.equal?("#{title}")
end

When /^I click to open navigation drawer$/ do
  base_page.clickByXpath("//android.widget.ImageButton[@index=0]")
end