PeakForest - REST
=======

Metadata
-----------

 * **@name**: PeakForest - REST
 * **@version**: 2.0
 * **@authors**: Nils Paulhe <nils.paulhe@inra.fr>
 * **@date creation**: 2014/02/11
 * **@main usage**: PeakForest database's Webservice REST 
 * **@see**: peakforest-api

Configuration
-----------

### Requirement:
 * Tomcat7+ server; Java JRE 8

### Deploy:
 * get project data `git clone ssh://git@pfemw3.clermont.inra.fr:dev-team/peakforest-rest.git`
 * config files:
    * `src/main/resources/conf.properties`
    * `src/main/resources/hibernate.cfg.xml`

### Warning:
Many REST requests require a token, send a GET parameter. 
To get a token, you need a PeakForest account!

Services provided
-----------
See user documentation on[git@pfemw3.clermont.inra.fr:dev-team/doc-metabohub.git](MetaboHUB documentation) project.


Technical description
-----------
...

Notes
-----------
...
