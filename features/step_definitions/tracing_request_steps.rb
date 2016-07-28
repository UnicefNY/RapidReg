Then /^I should see a tracing request with age "(.*?)"$/ do |age|
  base_page.verifyTextExist(age)
end

Then /^I should see the tracing request "(.*?)" is "(.*?)"$/ do |field, value|
  if value == "Today's date"
    time = Time.now
    value = time.strftime("%b %d, %Y")
  end
  actual_value = base_page.findById(field).text
  raise("Expect_value: #{value}, but actual_value: #{actual_value}") unless actual_value == value
end

