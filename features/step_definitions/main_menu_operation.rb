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

end

# type: # android.widget.ImageButton
# text:

# type: # android.widget.CheckedTextView
# text: Cases

# type: # android.widget.TextView
# text: Cases