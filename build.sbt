name := "SparkStreamimgDemo"

version := "1.0"

//scalaVersion := "2.12.2"

scalaVersion := "2.11.8"

//resolvers += Resolver.sonatypeRepo("snapshots")

/*
libraryDependencies ++= Seq(
  "org.apache.spark"       % "spark-core_2.10"              % "1.0.0",
  "org.apache.spark"       % "spark-sql_2.10"               % "1.0.0",
  "org.apache.spark"       % "spark-streaming_2.10"         % "1.0.0",
  "org.apache.spark"       % "spark-streaming-twitter_2.10" % "1.0.0"
)*/


libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.1.0"

libraryDependencies += "org.apache.spark" % "spark-streaming_2.11" % "2.1.0"

libraryDependencies += "org.apache.spark" % "spark-sql_2.11" % "2.1.0"