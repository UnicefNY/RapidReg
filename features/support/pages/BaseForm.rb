module Screen
  module Android
    class BaseForm < RapidRegAppPage

      def verifyFormValue(field, value)
        if value.include?("<Checkbox>")
          actual_value = findByXpath("//*[@text='#{field}']//parent::*//*[@resource-id='org.unicef.rapidreg.debug:id/value']").attribute('checked')
          if value.include?("Yes")
            raise ("'#{field}' is not checked") unless actual_value == 'true'
          else
            raise ("'#{field}' should not be checked") unless actual_value == 'false'
          end
        elsif value.include?("<Radio>")
          actual_value = findByXpath("//*[@text='Sex']//parent::*//android.widget.RadioGroup/*[@checked='true']").text
          option = value.split('>')[1].strip
          raise ("'#{field}': expect_value is '#{option}', but actual_value is '#{actual_value}'") unless actual_value == option
        else
          actual_value = findByXpath("//*[@text='#{field}']//parent::*//*[@resource-id='org.unicef.rapidreg.debug:id/value']").text
          raise ("'#{field}': expect_value is '#{value}', but actual_value is '#{actual_value}'") unless actual_value == value
        end
      end

    end
  end
end