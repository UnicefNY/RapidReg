module Screen
  module Android
    class BaseForm < RapidRegAppPage

      def fillInForm(field, value)
        if value.include?("<Select>")
          clickByXpath("//android.widget.TextView[@text='#{field}']")
          option = value.split('>')[1].strip
          clickByXpath("//*[@text='#{option}']//parent::*//*[@resource-id='org.unicef.rapidreg.debug:id/radioButton']")
          clickByXpath("//android.widget.Button[@text='OK']")
        elsif value.include?("<Checkbox>")
          if value.include?("Yes")
            clickByXpath("//*[@text='#{field}']//parent::*//*[@resource-id='org.unicef.rapidreg.debug:id/value']")
          end
        elsif value.include?("<Date>")
          clickByXpath("//android.widget.TextView[@text='#{field}']")
          clickByXpath("//android.widget.Button[@text='OK']")
        elsif value.include?("<Text>")
          clickByXpath("//android.widget.TextView[@text='#{field}']")
          text = value.split('>')[1].strip
          findByXpath("//android.widget.EditText").send_keys(text)
          clickByXpath("//android.widget.Button[@text='OK']")
        elsif value.include?("<Radio>")
          option = value.split('>')[1].strip
          clickByXpath("//*[@text='#{field}']//parent::*//*[@resource-id='org.unicef.rapidreg.debug:id/option_group']//*[@text='#{option}']")
        elsif value.include?("<Multiple>")
          clickByXpath("//android.widget.TextView[@text='#{field}']")
          str = value.split('>')[1].strip
          options = str.split(',')
          for i in 0...options.length
            clickByXpath("//android.widget.CheckedTextView[@text='#{options[i]}']")
          end
          clickByXpath("//android.widget.Button[@text='OK']")
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


      def editForm(field, old_value, new_value)
        if new_value.include?("<Radio>")
          option = new_value.split('>')[1].strip
          findByXpath("//android.widget.TextView[@text='#{field}']")
          clickByXpath("//android.widget.RadioButton[@text='#{option}']")
        else
          clickByXpath("//android.widget.EditText[@text='#{old_value}']")
          findByXpath("//android.widget.EditText[@text='#{old_value}']").clear
          findByXpath("//android.widget.EditText[@text='']").send_keys(new_value)
        end
      end

    end
  end
end