module Screen
  module Android
    class CasePage < RapidRegAppPage

      def createNewCase
        clickById("add_case")
      end

      def scrollToNextForm
        1.times { scrollPartScreen("left") }
      end

      def scrollToNextFields
        1.times { scrollPartScreen("down")}
        sleep 3
      end

      def saveCase
        clickById("save_case")
      end

      def getCurrentFormName
        return findByXpath("//android.widget.RelativeLayout/android.widget.HorizontalScrollView/android.widget.LinearLayout/
android.widget.TextView[@selected='true']").text
      end

      def fill_in(field, value)
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
          findByXpath("//android.widget.EditText").send_keys("#{value}")
          clickByXpath("//android.widget.Button[@text='OK']")
        end
      end

    end
  end
end


