Then /^I should see following:$/ do |table|
  table.rows_hash.each do |field, value|
    until base_page.ifTextExist(field) do
      case_page.scrollToNextFields
    end
    base_form.verifyFormValue(field,value)
  end
end