jruby-flume
===========
This is a proof-of-concept plugin library for flume which uses
jRuby code in flume sources, sinks and decorators. 

This project includes both the java classes
needed to embed jruby scripts as flume plumbing, as well as three demonstative
ruby scripts implementing a "helloworld" style source, sink and decorator on
the ruby side.

Within flume, the source/sink/decorator extensions accept a path to a ruby script
(and optional command line arguments) that will be evaluated at the setup of the
node's data pipeline. The return value from the script should be an object of a
class that can act as the appropriate type of event processor. If all goes well,
the node will start making calls to the ruby object whenever it needs to process
an event.


Dependencies
------------

* flume (originally developed with 0.9.1+29, but it will probably work with other versions)
* jruby


Getting Started
---------------

Follow the instructions to install flume plugins. Basically:

1. Modify your flume-conf.xml
       <!--- ================================================= -->
       <!--- Flume Plugins =================================== -->
       <!--- ================================================= -->
       <property>
         <name>flume.plugin.classes</name>
         <value>com.infochimps.flume.jruby.JRubyDecorator,com.infochimps.flume.jruby.JRubySource,com.infochimps.flume.jruby.JRubySink</value>
         <description>List of plugin classes to load.</description>
       </property>

2. Ensure that both jruby.jar and jruby-flume.jar are on the FLUME_CLASSPATH
when flume master and flume nodes are started.

3. Set up a flume data path:

       node: jRubySource("/path/to/source.rb") | { jRubyDecorator("/path/to/decorator.rb") => jRubySink("/path/to/sink.rb") }

 I have not tested this yet in distributed mode, but I suspect that the ruby 
 scripts will have to be located in the same local directory on every machine
 that expects to be able to use them.

4. Enjoy!
   
