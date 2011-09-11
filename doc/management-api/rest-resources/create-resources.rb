#!/usr/bin/env ruby

['rubygems', 'erubis', 'fileutils', 'json'].each { |lib| require lib }

CONFIG = JSON.parse(File.new('config.json').read)

module Erubis
  class Eruby
    def render(params)
      rendering = ""
      params[:collection].each do |part|
        rendering << Erubis::Eruby.new(File.new("#{params[:partial]}.tex.erb").read).result(part) << "\n\n\n"
      end
      rendering
    end
  end
end

class Resource

  attr_reader :resource

  def initialize(resource)
    @resource = resource
    prepend_base_url()
    replace_status_codes()
  end
  
  private
  
  def prepend_base_url
    @resource['endpoints'].each do |endpoint|
      endpoint['request']['url'].insert(0, CONFIG['base_url'])
    end
  end
  
  def replace_status_codes
    @resource['endpoints'].each do |endpoint|
      endpoint['response']['success_status_code'].each_with_index do |code, i|
        endpoint['response']['success_status_code'][i] = CONFIG["status_codes"][code]
      end
      endpoint['response']['failure_status_code'].each_with_index do |code, i|
        endpoint['response']['failure_status_code'][i] = CONFIG["status_codes"][code]
      end
    end
  end

end


if __FILE__ == $0
  
  # delete all .tex files in the directory
  Dir[File.dirname(__FILE__) + "/*.tex"].each do |file|
    FileUtils.rm_rf(file)
  end
  
  CONFIG['resources'].each_pair do |name, resource|
    File.open("#{name}.tex", 'w') do |f|
      f.write Erubis::Eruby.new(File.new('resource.tex.erb').read).result(Resource.new(resource).resource)
      puts "Wrote #{name}.tex"
    end
  end
  
end