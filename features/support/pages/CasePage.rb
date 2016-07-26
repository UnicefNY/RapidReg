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
        sleep 3
        return findByXpath("//android.widget.HorizontalScrollView/android.widget.LinearLayout/android.widget.TextView[@selected='true']").text
      end



      def fillInSearchField(field, value)
        if value.include?("<Date>")
          date = value.split('>')[1].strip
          findByXpath("//android.widget.EditText[@text='#{field}']").send_keys(date)
        else
          findByXpath("//android.widget.EditText[@text='#{field}']").send_keys(value)
        end
      end

    end
  end
end


