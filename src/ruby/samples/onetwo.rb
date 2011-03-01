# onetwo.rb --- jRubyDecorator script
require 'java'
java_import 'com.cloudera.flume.core.EventSinkDecorator'
java_import 'com.cloudera.flume.core.Event'
java_import 'com.cloudera.flume.core.EventImpl'
  
class OneTwoDecorator < EventSinkDecorator
  def table
    (@one = !@one) ? "one" : "two"
  end
 
  def append(e)
    attrs = java::util::HashMap.new e.getAttrs
    attrs["table"] = table.to_java_bytes
    values = String.from_java_bytes( e.getBody ).scan(/.../)
    values.each_index { |i| attrs["2hb_aaa:#{i}"] = values[i].to_java_bytes }
    attrs["2hb_"] = values[0].to_java_bytes if values[0]
    super EventImpl.new( e.getBody, e.getTimestamp, e.getPriority, e.getNanos, e.getHost, attrs )
  end
end
OneTwoDecorator.new( $context )
