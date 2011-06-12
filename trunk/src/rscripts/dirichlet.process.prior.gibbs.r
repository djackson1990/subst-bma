sampleSize<-2
alpha<-1000000
t<-c(0.1,0.2)
lik<-c(0.2,0.8)
rec<-vector(length=10000)
samples<-matrix(nrow=10000,ncol=length(t))

for(i in 1:nrow(samples)){
	update.index<-sample(c(1:length(t)),1)
	cluster.count<-length(unique(t[-update.index]))

	getFreqs<-function(val.vec,unique.val){
		freqs<-vector(length=length(unique.val))
		for(i in 1:length(freqs)){
			freqs[i]<-length(which(val.vec==unique.val[i]))
		}
		freqs
	}

	u<-unique(t[-update.index])

	freqs<-getFreqs(t[-update.index],u)
	full.cond<-vector(length=(cluster.count+sampleSize))
	for(j in 1:cluster.count){
		full.cond[j]<-freqs[j]*lik[round(u[j])+1]
	}
	
	pre.sample<-runif(sampleSize)
	if(length(which(u==t[update.index])) == 0){
		pre.sample[1]<-t[update.index]
	}
	for(j in 1:sampleSize){
		full.cond[cluster.count+j]<-alpha/sampleSize*
		lik[round(pre.sample[j])+1]
		
	}
	
	norm.full.cond<-full.cond/sum(full.cond)
	cand<-c(u,pre.sample)
	prop.val.index<-sample(1:length(cand),1,replace=T,prob=norm.full.cond)
	prop.val<-cand[prop.val.index]
	rec[i]<-prop.val.index	
	t[update.index]<-prop.val
	samples[i,]<-t

	#step 2
	update.index<-sample(c(1:length(t)),1)
	s<-runif(1)
	if(s<0.2){
		t[update.index]<-runif(1)*0.5
	}else{
		t[update.index]<-runif(1)*0.5+0.5
	}
	samples[i,]<-t

	if(length(which(is.na(t)))>0){
		stop()
	}
				
}


hist(samples[,1],prob=T)

length(which(samples[,1]>0.5))













cluster.counts<-vector(length=nrow(samples))
for(i in 1:length(cluster.counts)){
	cluster.counts[i]<-length(unique(round(samples[i,],5)))
}
table(cluster.counts)

hist(cluster.counts)
hist(samples[,1],prob=T)
nrow(samples[samples[,1]<0.5 & samples[,2]<0.5 &cluster.counts==1,])
nrow(samples[samples[,1]<0.5 & cluster.counts==1,])


nrow(samples[samples[,1]>0.5 & samples[,2]>0.5 &cluster.counts==1,])

nrow(samples[samples[,1]<0.5 & samples[,2]<0.5 &cluster.counts==2,])
nrow(samples[samples[,1]>0.5 & samples[,2]>0.5 &cluster.counts==2,])

nrow(samples[samples[,1]<0.5 & samples[,2]>0.5 &cluster.counts==2,])
nrow(samples[samples[,1]>0.5 & samples[,2]<0.5 &cluster.counts==2,])



nrow(samples[samples[,1]<0.50 & samples[,2]>0.5,])
nrow(samples[samples[,1]<0.5 & samples[,2]<0.5,])
nrow(samples[samples[,1]>0.50 & samples[,2]<0.5,])
nrow(samples[samples[,1]>0.50 & samples[,2]>0.5,])


nrow(samples[samples[,1]>0.50,])
nrow(samples[samples[,1]>0.50 &cluster.counts==1,])



samples[,2]


t<-c(0.16,0.16,0.04,0.64,0.04,0.64)*c(0.3,0.3,0.7,0.7,0.3,0.3)

t/sum(t)