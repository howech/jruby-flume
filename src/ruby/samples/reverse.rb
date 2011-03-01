# jRubyDecorator script
require 'java'
java_import 'com.cloudera.flume.core.EventSinkDecorator'
java_import 'com.cloudera.flume.core.Event'
java_import 'com.cloudera.flume.core.EventImpl'
class ReverseDecorator < EventSinkDecorator
  def append(e)
    body = String.from_java_bytes e.getBody
    super EventImpl.new( body.reverse.to_java_bytes, e.getTimestamp, e.getPriority, e.getNanos, e.getHost, e.getAttrs )
  end
end
ReverseDecorator.new( $context )
