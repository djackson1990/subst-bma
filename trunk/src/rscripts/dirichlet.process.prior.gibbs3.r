sampleSize<-5
alpha<-1000000
t<-c(0.1,0.2,0.7)
lik<-c(0.2,0.8)
rec<-vector(length=10000)
samples<-matrix(nrow=20000,ncol=length(t))
stepSize<-0.3

for(i in 1:(nrow(samples)/2)){
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
	samples[2*i-1,]<-t

	#step 2
	update.index<-sample(c(1:length(t)),1)
	curr.val<-t[update.index]
	dir<-sample(c(-1,1),1)	
	proposal<-curr.val+dir*runif(1)*0.3
	curr.post<-lik[round(curr.val)+1]
	if(proposal> 1 || proposal < 0){
		proposal.post<-0
	}else{
		proposal.post<-lik[round(proposal)+1]
	}
	r<-proposal.post/curr.post
	s<-runif(1)
	if(s<r){
		#accept
		t[t==curr.val]<-proposal
	}
	samples[2*i,]<-t

	
	if(length(which(is.na(t)))>0){
		stop()
	}
				
}





hist(samples[,1],prob=T,nclass=2)

length(which(samples[,1]>0.5))
length(which(samples[,1]<0.5))

length(which(samples[,1]))

cluster.counts<-vector(length=nrow(samples))
for(i in 1:length(cluster.counts)){
	cluster.counts[i]<-length(unique(round(samples[i,],5)))
}
table(cluster.counts)
