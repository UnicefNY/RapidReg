module Screen
  module Android
    class CasePage < RapidRegAppPage

      def switchToFullForm
        1.times { scrollFullScreen("up") }
      end

      def switchToMiniForm
        1.times { scrollFullScreen("down") }
      end

      def scrollToNextForm
        1.times { scrollPartScreen("left") }
      end

      def scrollLittleUp
        1.times { scrollPartScreen("little_up") }
      end

      def scrollToNextFields
        1.times { scrollPartScreen("up") }
        sleep 3
      end

      def scrollToPreviousFields
        1.times { scrollPartScreen("down") }
      end

      def getCurrentFormName
        return findByXpath("//android.widget.TextView[@selected='true']").text
      end

      def fillInForm(field, value)
        if value.include?("<Select>")
          option = value.split('>')[1].strip
          clickByXpath("//android.widget.TextView[@text='#{field}']")
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
        elsif value.include?("<Text>")
          text = value.split('>')[1].strip
          clickByXpath("//android.widget.TextView[@text='#{field}']")
          findByXpath("//android.widget.EditText").send_keys(text)
          clickByXpath("//android.widget.Button[@text='OK']")
        elsif value.include?("<Radio>")
          option = value.split('>')[1].strip
          findByXpath("//android.widget.TextView[@text='#{field}']")
          clickByXpath("//android.widget.RadioButton[@text='#{option}']")
        else
          clickByXpath("//android.widget.TextView[@text='#{field}']")
          findByXpath("//android.widget.EditText[@text='']").send_keys(value)
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

      def fillInMiniForm(case_instance)
        p case_instance
        raise("Case dose not exist...") if case_instance.empty?
        fullname = case_instance["Full Name"]
        sex = case_instance["Sex"]
        age = case_instance["Age"]
        fillInForm("Full Name", fullname)
        fillInForm("Sex", sex)
        fillInForm("Age", age)
      end


      def verifyFormValue(field, value)
        element = findByXpath("//android.widget.TextView[@text='#{field}']")
        puts "'#{element.text}':"
        sleep 2
        scrollLittleUp
        actual_value = findByXpath("//android.widget.EditText[@text='#{value}']").text
        puts actual_value
      end

    end
  end
end


