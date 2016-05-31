module LocatorModule

  def findByName(name)
    find_element(:name, "#{name}")
  end

  def findById(id)
    find_element(:id, "#{id}")
  end

  def findByXpath(xpath)
    find_element(:xpath, "#{xpath}")
  end

  def clickByName(name)
    waitElement { findByName(name).click }
  end

  def clickById(id)
    waitElement { findById(id).click }
  end

  def verifyTextNotExist(text)
    raise("Should not find text: #{text}") unless texts(text).empty?
  end

  def verifyTextExist(text)
    raise("Not find text: #{text}") if texts(text).empty?
  end

  def verigyPromptNotExist(message)
    raise("Should not find text: #{message}") unless waitElement{ texts(message).empty? }
  end

  def verifyPromptExist(message)
    raise("Not find text: #{message}") if waitElement { texts(message).empty? }
  end

  def verifyButtonNotExist(button_name)
    raise("Should not find button: #{button_name}") if exists { button(button_name) }
  end

  def verifyButtonExist(button_name)
    raise("Not find button: #{button_name}") unless exists { button(button_name) }
  end

  def waitElement
    timeout = 30
    polling_interval = 0.2
    time_limit = Time.now + timeout
    loop do
      begin
        yield
      rescue Exception => error
      end
      return if error.nil?
      raise error if Time.now >= time_limit
      sleep polling_interval
    end
  end

end

class RapidRegAppPage
  include LocatorModule
end