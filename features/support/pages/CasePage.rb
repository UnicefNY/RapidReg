module Screen
  module Android
    class CasePage < RapidRegAppPage

      def scrollToNextForm
        1.times { scrollPartScreen("left") }
      end

      def scrollToNextFields
        1.times { scrollPartScreen("down")}
        sleep 3
      end

      def getCurrentFormName
        return findByXpath("//android.widget.RelativeLayout/android.widget.HorizontalScrollView/android.widget.LinearLayout/
android.widget.TextView[@selected='true']").text
      end

      def fillInForm(field, value)
        if value.include?("<Select>")
          clickByXpath("//android.widget.TextView[@text='#{field}']")
          option = value.split('>')[1].strip
          clickByXpath("//android.widget.CheckedTextView[@text='#{option}']")
          clickByXpath("//android.widget.Button[@text='OK']")
        elsif value.include?("<Checkbox>")
          if value.include?("Yes")
            findByXpath("//android.widget.LinearLayout/android.widget.TextView[@text='#{field}']")
            clickByXpath("//android.widget.LinearLayout/android.widget.CheckBox")
          end
        elsif value.include?("<Date>")
          clickByXpath("//android.widget.TextView[@text='#{field}']")
          clickByXpath("//android.widget.Button[@text='OK']")
        else
          clickByXpath("//android.widget.TextView[@text='#{field}']")
          findByXpath("//android.widget.EditText").send_keys(value)
          clickByXpath("//android.widget.Button[@text='OK']")
        end
      end

      def fillInSearchField(field, value)
        if value.include?("<Date>")
          date = value.split('>')[1].strip
          findByXpath("//android.widget.EditText[@text='#{field}']").send_keys(date)
        else
          findByXpath("//android.widget.EditText[@text='#{field}']").send_keys(value)
        end
      end

      def createCaseThroughMiniForm(case_instance)
        p case_instance
        raise("Case dose not exist...") if case_instance.empty?
        fullname = case_instance["Full Name"]
        sex = case_instance["Sex (Required)"]
        age = case_instance["Age (Required)"]
        fillInForm("Full Name", fullname)
        fillInForm("Sex (Required)", sex)
        fillInForm("Age (Required)", age)
      end

    end
  end
end


