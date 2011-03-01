# jruby class that acts as a flume decorator
require 'java'
java_import 'com.cloudera.flume.core.EventSinkDecorator'
java_import 'com.cloudera.flume.core.Event'
java_import 'com.cloudera.flume.core.EventImpl'

# Define a class that extends the EventSinkDecorator class
class Decorator < EventSinkDecorator
 def append(e)
   # Append the body of the event to the end of a friendly greeting from Ruby.
   hello_from_ruby = "Hello from jRuby! == " + String.from_java_bytes( e.getBody )

   # The attributes Map that is passed to us is read only. We need to build a
   # new one that we can write to. Lucky for us, HashMap has a copy contructor
   # that does the trick.
   attrs = java.util.HashMap.new( e.getAttrs )

   # Also on the ruby side of the world, a java HashMap works like a hash.
   # Remember that the values need to be java arrays of bytes, so the to_java_bytes
   # method does the grunt work in the conversion.
   attrs["jruby"] = "yup".to_java_bytes

   # Build a new EventImpl using our generated string and new attributes.
   e2 = EventImpl.new( hello_from_ruby.to_java_bytes, e.getTimestamp, e.getPriority, e.getNanos, e.getHost, attrs )

   # Pass the event out to the superclass - it will know how to push it downstream.
   super e2
 end
end

# Create a new instance of the class. As this is the last expression in the file
# it will get returned as the value of the "eval" function on the java side of
# the world.

Decorator.new( $context )
