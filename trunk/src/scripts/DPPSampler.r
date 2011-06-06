sampleSize<-1
alpha<-1
t<-c(0.1,0.2,0.3)

rec<-vector(length=100000)
samples<-matrix(nrow=100000,ncol=length(t))

for(i in 1:nrow(samples)){
	update.index<-sample(c(1,2),1)
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
		full.cond[j]<-freqs[j]/(length(t)-1+alpha)
	}
	
	pre.sample<-runif(sampleSize)
	for(j in 1:sampleSize){
		full.cond[cluster.count+j]<-alpha/sampleSize/(length(t)-1+alpha)
		
	}
	
	norm.full.cond<-full.cond/sum(full.cond)
	cand<-c(u,pre.sample)
	prop.val.index<-sample(1:length(cand),1,replace=T,prob=norm.full.cond)
	prop.val<-cand[prop.val.index]
	rec[i]<-prop.val.index	
	t[update.index]<-prop.val
	samples[i,]<-t

	if(length(which(is.na(t)))>0){
		stop()
	}
				
}

cluster.counts<-vector(length=nrow(samples))
for(i in 1:length(cluster.counts)){
	cluster.counts[i]<-length(unique(round(samples[i,],5)))
}
table(cluster.counts)

hist(cluster.counts)


col1<-unique(samples[,1])

cond.col1<-vector(length=length(col1))
for(i in 1:length(cond.col1)){
	sub<-samples[samples[,1]==col1[i],]
	counter= 0
	for(j in 1:nrow(sub)){
		if(length(unique(sub[j,]))>1){
			counter = counter+1
		}
		
	}
	cond.col1[i]<-counter/nrow(sub)
}


s<-c(1,2,2,1,4,1,2,3)
u<-c(1:4)
getFreqs(s,u)
table(s)
getFreqs<-function(val.vec,unique.val){
	freqs<-vector(length=length(unique.val))
	for(i in 1:length(freqs)){
		freqs[i]<-length(which(val.vec==unique.val[i]))
	}
	freqs
}


length(unique(samples[,1]))

table(cluster.counts)

table(samples[,1])
table(samples[,2])
sum(table(samples[,2])*states)/nrow(samples)

nrow(samples[samples[,1]==0 & samples[,2]==0,])
nrow(samples[samples[,1]==0 & samples[,2]==1,])
nrow(samples[samples[,1]==1 & samples[,2]==0,])
nrow(samples[samples[,1]==1 & samples[,2]==1,])


samples[,2]