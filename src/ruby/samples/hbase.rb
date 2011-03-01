# hbase.rb --- jRubySink script
# This script dynamically creates Attr2HBaseEventSink instances
# based on the value of an event's "table" attribute.
require 'java'
java_import 'com.cloudera.flume.core.Event'
java_import 'com.cloudera.flume.core.EventImpl'
java_import 'com.infochimps.flume.jruby.JRubySink'
java_import 'com.cloudera.flume.hbase.Attr2HBaseEventSink'

# Define a class that extends the EventSink.Base class
class HBase < JRubySink
  def initialize
    super
    # Make sure that we have an available empty hash for storing
    # connections to tables.
    @tables = {}
  end

  def get_table( table )
    unless @tables[table]
      # Create a new Attr2HBaseEventSink instance
      # @tables[table] = Attr2HBaseEventSink.new(table,"",false,"","","2hb_",0,true,true) # 
      # unhacked version
      @tables[table] = Attr2HBaseEventSink.new(table,"",false,"","","2hb_",0,true)
      @tables[table].open
    end
    @tables[table]
  end

  def append(e)
    table = String.from_java_bytes e.getAttrs[ "table" ]
    throw new IOException( "No table!" ) unless table

    sink = get_table(table)
    throw new IOException( "No sink!" ) unless sink
    sink.append(e)
  end

  def close
    # Close any of the dynamic sinks that we opened
    @tables.each_value do |sink|
      sink.close
    end
    # Forget about the old sinks - create new ones on demand
    @tables={}
    super
  end
end

#Create an instance and return it to the eval function back in java land.
HBase.new

