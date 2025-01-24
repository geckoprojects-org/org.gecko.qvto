/home/grune/git/org.gecko.qvto/org.eclipse.m2m.qvt.oml
/home/grune/git/org.gecko.qvto/org.eclipse.m2m.qvt.oml.common
/home/grune/git/org.gecko.qvto/org.eclipse.m2m.qvt.oml.cst.parser
/home/grune/git/org.gecko.qvto/org.eclipse.m2m.qvt.oml.ecore.imperativeocl
/home/grune/git/org.gecko.qvto/org.eclipse.m2m.qvt.oml.emf.util
/home/grune/git/org.gecko.qvto/org.eclipse.m2m.qvt.oml.ocl
/home/grune/git/org.gecko.qvto/org.eclipse.m2m.qvt.oml.runtime

## dependencies

lpg.runtime:java:2.0.17-v201004271640

## critical
| project	 | package				 | bundle			|
| oml.common | org.eclipse.osgi.util | org.eclipse.osgi	|


## org.eclipse.ocl.ecore

### ProjectDependencyTracker
Verwendung von EclipsePlugin + Logging ausgebaut  
Extensionpoint handling für qvtProjectDependencyTracker deaktiviert

## org.eclipse.ocl.common

### OCLCommon
Extensionpoint handling für validationdelegate deaktiviert
IPreferenceChangeListener deaktiviert

## org.eclipse.ocl

### EnvironmentRegistryImpl
Platform.getExtensionRegisty ersetzt

### OCLPlugin
Verwendung von EclipsePlugin + Logging ausgebaut  

## org.eclipse.m2m.qvt.oml.common

### ResourceSetProviderRegistry
Extensionpoint handling für ResourceSetProvider deaktiviert

### TransformationRegistry
Extensionpoint handling für DeployedTransformation deaktiviert

## org.eclipse.m2m.qvt.oml.ocl
### OclQvtoPlugin
Extensionpoint handling für LibrariesRegistry deaktiviert
Logger customization deaktiviert

### LibraryImpl
Platform.getBundle durch FrameworkUtil.getBundle ersetzt

### Libraries 
Plugin entfernt

## org.eclipse.m2m.qvt.oml.emf.util

### EmfUtilPlugin
Plugin entfernt

### Logger
EclipsePlugin log deaktiviert 

### URIUtils
getResource deaktiviert da Workspace Root verwendet 

### WorkspaceUtils
ohne Workspace

## org.eclipse.m2m.qvt.oml.ecore.imperativeocl

### ImperativeOCL
EMFPlugin durch ResourceLocator ersetzt

## org.eclipse.m2m.qvt.oml.common

### CommonPlugin
Plugin, ResourceBundle und Logging entfernt

### IOResource
ResourcesPlugin.getEncoding() auf Charset.defaultCharset().name()

### EmptyDebugTarget
entfernt

### ProcessJob
JobChangeListener und Plugin.log entfernt

### ShallowProcess
enfernt

### TransformationRegistry
ExtensionPoint handling für DeployedTransformation entfernt

### ResourceSetProviderRegistry
ExtensionRegistry change listener und refresh entfernt

## org.eclipse.m2m.qvt.oml
### CompilerUtils
URIUtil.getResources deaktiviert

### UnitResolverFactory
ExtensionPoint handling für unitResolverFactory entfernt

### BundleBlackboxProvider
ExtensionPoint handling für javaBlackboxUnits entfernt

### ProjectMetamodelRegistryProvider
remove Workspace access

## org.eclipse.m2m.qvt.oml.runtime

### QvtRuntimePlugin
deaktivate workspace 

### PlatformPluginUnitResolver
extensionpoint handling für qvtTransformationContainer

### ProjectDependencyTracker
extensionpoint handling für org.eclipse.m2m.qvt.oml.runtime.qvtProjectDependencyTracker

# removed functionality
* IProgressMonitor
* ILaunch and ILaunchConfiguration

# moving eclipse specific 
qvt.oml.common -> qvt.oml.common.eclipse