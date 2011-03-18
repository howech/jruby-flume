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

1. Modify your flume-site.xml
       <!--- ================================================= -->
       <!--- Flume Plugins =================================== -->
       <!--- ================================================= -->
       <property>
         <name>flume.plugin.classes</name>
         <value>com.infochimps.flume.jruby.JRubyDecorator,com.infochimps.flume.jruby.JRubySource,com.infochimps.flume.jruby.JRubySink</value>
         <description>List of plugin classes to load.</description>
       </property>

2. Ensure that both jruby.jar and jruby-flume.jar are on the FLUME_CLASSPATH
when flume master and flume nodes are started. (Or, you can drop these jars in the /usr/lib/flume/lib directory...)

3. Set up a flume data path:

       node: jRubySource("/path/to/source.rb") | { jRubyDecorator("/path/to/decorator.rb") => jRubySink("/path/to/sink.rb") }

4. Enjoy!


Notes
-----

If you need access to a flume context object, you can now get to it through the "$context" global variable. Also, additional arguments to the script are available through the "$args" global variable.

If you prefer to use relative paths for script names, they should start from your "FLUME_HOME" directory. By default this 
will be something like "/usr/lib/flume". If you follow the convention of making a scripts directory in that directory, you can
access your scripts by "jRubyDecorator("scripts/some_script.rb").

In distributed mode, you need to have scripts deployed to both the master node and to the nodes that will run the scripts. 


