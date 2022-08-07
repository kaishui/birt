# how to get started guide with birt
Please following the below guide to setup dev env.
```
https://www.youtube.com/watch?v=FqfrG2I0AIw
https://wiki.eclipse.org/Eclipse_Platform_SDK_Provisioning
https://www.eclipse.org/setups/installer/?url=https://raw.githubusercontent.com/eclipse/birt/master/build/org.eclipse.birt.releng/BIRTConfiguration.setup&show=true
```

# how to read the structure of birt project 
```
https://wiki.eclipse.org/BIRT/FAQ/Birt_Project
```

# hit some issue and solution
## 1. Caused by: java.lang.NoClassDefFoundError: org/eclipse/swt/widgets/Widget

```
answer: https://stackoverflow.com/questions/17527096/eclipse-kepler-startup-error-noclassdeffounderror-org-eclipse-swt-widgets-disp

1. Click Run -> Run Configurations...
2. Select the Run Configuration that is failing (on the left side)
3. Click the Plug-ins tab
4. In the "type filter text" box, type swt
5. Select the proper swt package for your machine.  For me on OS X this was:
org.eclipse.swt.cocoa.macosx.x86_64

```

## 2. Caused by: java.lang.NoClassDefFoundError: org/eclipse/swt/SWTError

```
https://stackoverflow.com/questions/11959334/noclassdeffounderror-org-eclipse-swt-swterror
```

##