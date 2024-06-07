require 'json'

Pod::Spec.new do |s|
  package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

  s.name         = "RNVizbeeSenderSdk"
  s.version      = package['version']
  s.summary      = package['description']
  s.description  = <<-DESC
                  RNVizbeeSenderSdk
                   DESC
  s.author        = { "Vizbee" => "info@vizbee.tv" }
  s.homepage     = "https://vizbee.tv"
  s.license      = "MIT"
  s.platform     = :ios, "9.0"
  s.requires_arc = true
  s.source       = { :git => "https://github.com/author/RNVizbeeSenderSdk.git", :tag => "master" }
  s.source_files  = "ios/**/*.{h,m}"

  s.dependency "React"
  s.dependency 'VizbeeKit', '6.5.9'
  s.dependency 'google-cast-sdk-no-bluetooth-dynamic', '4.8.0'

end

  