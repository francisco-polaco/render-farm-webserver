# JVM Options
```-XX:-UseSplitVerifier```

# render-farm-webserver
```mvn install:install-file -Dfile=lib/BIT.jar -DgroupId=BIT -DartifactId=BIT -Dversion=1.0.0 -Dpackaging=jar```


# Credentials AWS
```
Create a file called "credentials" in the .aws folder containing:
[default]
aws_access_key_id=<your-aws-access-key-id>
aws_secret_access_key=<your-aws-secret-access-key>
```