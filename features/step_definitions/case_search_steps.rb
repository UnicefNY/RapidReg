And /^I search for:$/ do |table|
  table.rows_hash.each do |field, value|
    case_page.fillInSearchField(field, value)
  end
end

Then /^I should see following information:$/ do |table|
  table.rows_hash.each do |text|
    base_page.verifyTextExist(text)
  end
end

And /^I create case "(.*?)"$/ do |case_instance|
  base_page.clickById("fab_expand_menu_button")
  base_page.clickById("add_case")
  case_page.fillInMiniForm(test_case[case_instance])
  base_page.clickById("save_case")
end

And /^I clear up above "(.*?)" search conditions$/ do |num|
  num.to_i.times { base_page.clickById("clear_text") }
end


=begin
type: android.widget.EditText
text: ID

id: fab_expand_menu_button
id: add_tracing_request
id: add_case
=end