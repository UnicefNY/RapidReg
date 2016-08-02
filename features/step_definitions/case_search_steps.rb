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
  base_page.clickById("add")
  addCaseThroughMiniForm(test_case[case_instance])
  base_page.clickById("save")
end

And /^I clear up above "(.*?)" search conditions$/ do |num|
  num.to_i.times { base_page.clickById("clear_text") }
end

def addCaseThroughMiniForm(case_instance)
  p case_instance
  raise("Case dose not exist...") if case_instance.empty?
  fullname = case_instance["Full Name"]
  sex = case_instance["Sex"]
  age = case_instance["Age"]

  fillInMiniForm("Full Name", fullname)
  fillInMiniForm("Sex", sex)
  fillInMiniForm("Age", age)
end

def fillInMiniForm(field,value)
  until base_page.ifTextExist(field) do
    case_page.scrollToNextFields
  end
  case_page.scrollLittleUp
  puts field
  base_form.fillInForm(field, value)
  puts value
end