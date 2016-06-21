When /^I press "New Case" button$/ do
  case_page.createNewCase
end

# TODO:Refactor
And /^I scroll to "(.*?)" form$/ do |form_name|
  case_page.scrollToNextForm
  form = base_page.findByXpath("//android.widget.RelativeLayout/android.widget.HorizontalScrollView/android.widget.LinearLayout/
android.widget.TextView[@selected='true']")
  puts form.text == form_name
end

When /^I fill in the following:$/ do |table|
  table.rows_hash.each do |field, value|
    if base_page.ifTextExist(field)
      fill_in(field, value)
    else
      case_page.scrollToNextFiedls
      fill_in(field, value)
    end
  end
end

And /^I press "SAVE" button$/ do
  case_page.saveCase
end

Then /^I should see a new case with sex "(.*?)" and age "(.*?)"$/ do |sex,age|
  base_page.verifyTextExist(sex)
  base_page.verifyTextExist(age)
end


def fill_in(field, value)
  field_path = "//android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.support.v4.widget.DrawerLayout/
android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.RelativeLayout/android.support.v4.view.ViewPager/
android.widget.LinearLayout/android.widget.ListView/android.widget.LinearLayout"

  if value.include?("<Select>")
    base_page.clickByXpath("#{field_path}" + "/android.widget.TextView[@text='#{field}']")
    option = value.split('>')[1].strip
    base_page.clickByXpath("//android.widget.CheckedTextView[@text='#{option}']")
    base_page.clickByXpath("//android.widget.Button[@text='OK']")
  elsif value.include?("<Checkbox>")
    if value.include?("Yes")
      base_page.findByXpath("#{field_path}" + "/android.widget.LinearLayout/android.widget.TextView[@text='#{field}']")
      base_page.clickByXpath("#{field_path}" + "/android.widget.LinearLayout/android.widget.CheckBox")
    end
  elsif value.include?("<Date>")
    base_page.clickByXpath("#{field_path}" + "/android.widget.TextView[@text='#{field}']")
    base_page.clickByXpath("//android.widget.Button[@text='OK']")
  else
    base_page.clickByXpath("#{field_path}" + "/android.widget.TextView[@text='#{field}']")
    base_page.findByXpath("//android.widget.EditText").send_keys("#{value}")
    base_page.clickByXpath("//android.widget.Button[@text='OK']")
  end

end


=begin
base_path = "//android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.support.v4.widget.DrawerLayout/
android.widget.LinearLayout/android.widget.RelativeLayout[1]/android.widget.RelativeLayout/android.support.v4.view.ViewPager/
android.widget.LinearLayout/android.widget.ListView/android.widget.LinearLayout"

-----------------------------Select-------------------------
  xpath = "//android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.support.v4.widget.DrawerLayout/
android.widget.LinearLayout/android.widget.RelativeLayout[1]/android.widget.RelativeLayout/android.support.v4.view.ViewPager/
android.widget.LinearLayout/android.widget.ListView/android.widget.LinearLayout
/android.widget.TextView[@text='Case Status']"

android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/
android.widget.ListView/

  xpath = "//android.widget.FrameLayout/android.widget.FrameLayout/android.widget.LinearLayout/android.widget.LinearLayout/
android.widget.ListView/android.widget.CheckedTextView[@text='Open']"
type: android.widget.CheckedTextView
text: Open

type: android.widget.Button
text: OK

----------------------------Checkbox------------------------
  xpath = "//android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.support.v4.widget.DrawerLayout/
android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.RelativeLayout/android.support.v4.view.ViewPager/
android.widget.LinearLayout/android.widget.ListView/android.widget.LinearLayout
/android.widget.LinearLayout/android.widget.TextView[@text='#{checkbox}']"

  base_page.findByXpath("#{xpath}" + "/android.widget.TextView[@text='#{checkbox}']")
  base_page.clickByXpath("#{xpath}" + "/android.widget.CheckBox")

----------------------------Choose-----------------------------
  xpath = "//android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.support.v4.widget.DrawerLayout/
android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.RelativeLayout/android.support.v4.view.ViewPager/
android.widget.LinearLayout/android.widget.ListView/android.widget.LinearLayout
/android.widget.TextView[@text='Current Location']

type: android.widget.CheckedTextView
text: Sierra Leone::Western

type: android.widget.Button
text: OK

-----------------------------Date------------------------------
  xpath = "//android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.support.v4.widget.DrawerLayout/
android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.RelativeLayout/android.support.v4.view.ViewPager/
android.widget.LinearLayout/android.widget.ListView/android.widget.LinearLayout
/android.widget.TextView[@text='text: Date of Birth']

  type: android.widget.Button
  text: OK



-----------------------------------------------------------------
#   field_xpath = "//android.widget.FrameLayout/android.widget.LinearLayout/android.widget.FrameLayout/android.support.v4.widget.DrawerLayout/
# android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.RelativeLayout/android.support.v4.view.ViewPager/
# android.widget.LinearLayout/android.widget.ListView/android.widget.LinearLayout"

#   base_page.clickByXpath("#{field_xpath}" + "/android.widget.TextView[@text='#{field}']")
#   base_page.findByXpath("//android.widget.EditText").send_keys("#{value}")
#   base_page.clickByXpath("//android.widget.Button[@text='OK']")
=end


