# CMU-18645-How-To-Write-Fast-Code

For the term project
We get the data from [MovieLens](http://grouplens.org/datasets/movielens/)

## 1. Local Machine
[use python](http://blog.csdn.net/ygrx/article/details/15501679)

how to run: 
> python userFC.py ../../ml-100k/u.data ../../ml-100k/u.item

The above method use cos distance to calculate similarity and use user-based model, but in order to compared with the distributed version that use concurrency matrix as similarity and item-based model, we rewrite the version as itemCF.py.
To run it:
> python itemCF.py ./ml-100k/u.data ./ml-100k/u.item ./ml-100k/u.user


## 2. MapReduce Version
[use mahout](https://mahout.apache.org/users/recommender/userbased-5-minutes.html)

### 2.1 how to use maven
[maven toturial](http://www.07net01.com/2015/11/969628.html)
### 2.2 mahout in maven
[mahout toturial](http://blog.fens.me/hadoop-mahout-maven-eclipse/)



## 3. Spark Version
### 3.1. About the deployment of the maven for scalar
all the dependency can be found here: [dependency](http://mvnrepository.com/).
Be care about the version. If it is incorrect, there will be error.

for the pom.xml
In order to use spark, you should include dependency of scala-library, spark-core and spark-mllib.
In order to include all the dependency in the jar file you should add:
```xml
<plugin>
	<artifactId>maven-assembly-plugin</artifactId>
	<version>2.4.1</version>
	<configuration>
		<descriptorRefs>
			<descriptorRef>jar-with-dependencies</descriptorRef>
		</descriptorRefs>
	</configuration>
	<executions>
		<execution>
			<id>make-assembly</id>
			<phase>package</phase>
			<goals>
				<goal>single</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```

### 3.2. run on local machine
We use spark-submit to run it, so we don't have to use the jar with dependency.
> ~/spark-1.6.0/bin/spark-submit --class MovieLensALS target/guo-0.0.1-SNAPSHOT.jar

you can know what is inside a jar package with the command
> jar -tf XXXX.jar

### 3.3. run on AWS EMR
you can choose to add a step or login into the master node and use spark-submit.
