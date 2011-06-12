probs<-c(0.2,0.8)
sampleSize<-5
states<-c(0,1)
alpha<-1
t<-c(0,1)

samples<-matrix(nrow=1000000,ncol=length(t))
for(i in 1:nrow(samples)){
	update.index<-ceiling(runif(1)*length(t))
	cluster.count<-length(unique(t[-update.index]))
	freqs<-table(t[-update.index])
	full.cond<-vector(length=(cluster.count+sampleSize))
	for(j in 1:cluster.count){
		full.cond[j]<-freqs[j]/(length(t)-1+alpha)
	}
	
	pre.sample<-sample(states,sampleSize,replace=T,prob=probs)
	for(j in 1:sampleSize){
		full.cond[cluster.count+j]<-alpha/sampleSize/(length(t)-1+alpha)
		
	}
	
	norm.full.cond<-full.cond/sum(full.cond)
	prop.val<-sample(c(as.numeric(names(freqs)),pre.sample),1,replace=T,prob=norm.full.cond)
	t[update.index]<-prop.val
		

	
	samples[i,]<-t
	if(length(which(is.na(t)))>0){
		stop()
	}
			
	
}

table(samples[,1])
table(samples[,2])
sum(table(samples[,2])*states)/nrow(samples)

nrow(samples[samples[,1]==0 & samples[,2]==0,])
nrow(samples[samples[,1]==0 & samples[,2]==1,])
nrow(samples[samples[,1]==1 & samples[,2]==0,])
nrow(samples[samples[,1]==1 & samples[,2]==1,])


samples[,2]