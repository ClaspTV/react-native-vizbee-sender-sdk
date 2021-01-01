
Pod::Spec.new do |s|
  s.name         = "RNVizbeeSenderSdk"
  s.version      = "1.0.0"
  s.summary      = "RNVizbeeSenderSdk"
  s.description  = <<-DESC
                  RNVizbeeSenderSdk
                   DESC
  s.homepage     = "https://vizbee.tv"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNVizbeeSenderSdk.git", :tag => "master" }
  s.source_files  = "RNVizbeeSenderSdk/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  