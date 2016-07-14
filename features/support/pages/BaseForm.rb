module Screen
  module Android
    class BaseForm < RapidRegAppPage

      def fillInForm(field, value)
        if value.include?("<Select>")
          option = value.split('>')[1].strip
          clickByXpath("//android.widget.TextView[@text='#{field}']")
          clickByXpath("//android.widget.CheckedTextView[@text='#{option}']")
          clickByXpath("//android.widget.Button[@text='OK']")
        elsif value.include?("<Checkbox>")
          if value.include?("Yes")
            clickByXpath("//*[@text='#{field}']//parent::*//*[@resource-id='org.unicef.rapidreg.debug:id/value']")
          end
        elsif value.include?("<Date>")
          clickByXpath("//android.widget.TextView[@text='#{field}']")
          clickByXpath("//android.widget.Button[@text='OK']")
        elsif value.include?("<Text>")
          text = value.split('>')[1].strip
          clickByXpath("//android.widget.TextView[@text='#{field}']")
          sleep 5
          findByXpath("//android.widget.EditText").send_keys(text)
          clickByXpath("//android.widget.Button[@text='OK']")
        elsif value.include?("<Radio>")
          option = value.split('>')[1].strip
          clickByXpath("//*[@text='#{field}']//parent::*//*[@resource-id='org.unicef.rapidreg.debug:id/option_group']//*[@text='#{option}']")
        else
          clickByXpath("//*[@text='#{field}']")
          sleep 5
          findByXpath("//*[@text='#{field}']//parent::*//*[@resource-id='org.unicef.rapidreg.debug:id/value']").send_keys(value)
        end
      end


      def verifyFormValue(field, value)
        if value.include?("<Checkbox>")
          actual_value = findByXpath("//*[@text='#{field}']//parent::*//*[@resource-id='org.unicef.rapidreg.debug:id/value']").attribute('checked')
          if value.include?("Yes")
            raise ("'#{field}' is not checked") unless actual_value == 'true'
          else
            raise ("'#{field}' should not be checked") unless actual_value == 'false'
          end
        elsif value.include?("<Radio>")
          actual_value = findByXpath("//*[@text='#{field}']//parent::*//android.widget.RadioGroup/*[@checked='true']").text
          option = value.split('>')[1].strip
          raise ("'#{field}': expect_value is '#{option}', but actual_value is '#{actual_value}'") unless actual_value == option
        else
          sleep 5
          actual_value = findByXpath("//*[@text='#{field}']//parent::*//*[@resource-id='org.unicef.rapidreg.debug:id/value']").text
          raise ("'#{field}': expect_value is '#{value}', but actual_value is '#{actual_value}'") unless actual_value == value
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

      def editForm(field, old_value, new_value)
        if new_value.include?("<Radio>")
          option = new_value.split('>')[1].strip
          findByXpath("//android.widget.TextView[@text='#{field}']")
          clickByXpath("//android.widget.RadioButton[@text='#{option}']")
        else
          findByXpath("//android.widget.EditText[@text='#{old_value}']").clear
          findByXpath("//android.widget.EditText[@text='']").send_keys(new_value)
        end
      end

    end
  end
end