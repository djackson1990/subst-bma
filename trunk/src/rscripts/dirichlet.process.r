##This script is a practice for understanding the dirichlet process.

theta.state.count<-100
vector.length<-3

probs<-runif(100)
probs<-probs/sum(probs)

distr<-dirichlet.process(theta.state.count,vector.length,probs)
##As state count approaches infinity, the sum of the vector returned approches to 1.0.
##This is because the model has infinite countable clusters.

dirichlet.process<-function(theta.state.count,vector.length,probs){

	vector.count<-theta.state.count^vector.length
	all.vectors<-array(dim=c(vector.count,vector.length))
	for(i in 1:vector.length){
		all.vectors[,i]<-rep(c(1:theta.state.count),rep(theta.state.count^(vector.length-i),theta.state.count))
	}

	alpha<-1
	distr<-vector(length=vector.count)
	for(i in 1:vector.count){
		clusters<-unique(all.vectors[i,])
		cluster.count<-length(clusters)
		distr[i]<-1
		for(j in 1:cluster.count){
			cluster.size<-length(which(all.vectors[i,]==clusters[j]))
			temp<-alpha*factorial(cluster.size-1)*probs[clusters[j]]
			distr[i]<-distr[i]*temp
		}
	}

	normalizing.const<-1
	for(i in 1:vector.length){
		normalizing.const<-normalizing.const*(i-1+alpha)
	}

	distr<-distr/normalizing.const
	distr
}

