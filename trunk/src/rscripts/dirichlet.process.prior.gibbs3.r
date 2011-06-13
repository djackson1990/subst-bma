sampleSize<-1
alpha<-1
t<-c(0.1,0.2)
lik<-c(0.2,0.8)
rec<-vector(length=100000)
samples<-matrix(nrow=200000,ncol=length(t))
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
	curr.post<-1
	for(j in 1:length(t)){
		curr.post<-curr.post*lik[round(t[j])+1]
	}
	if(proposal> 1 || proposal < 0){
		proposal.post<-0
	}else{
		t2<-t
		t2[t==curr.val]<-proposal
		proposal.post = 1
		for(j in 1:length(t2)){
			proposal.post<-proposal.post*lik[round(t2[j])+1]
		}
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

mean(samples[,2])

q<-samples[,2]

length(which(samples[,1]>0.5))
length(which(samples[,1]<0.5))


length(which(samples[,2]>0.5))
length(which(samples[,2]<0.5))

#length(which(samples[,3]>0.5))
#length(which(samples[,3]<0.5))





cluster.counts<-vector(length=nrow(samples))
for(i in 1:length(cluster.counts)){
	cluster.counts[i]<-length(unique(round(samples[i,],5)))
}
table(cluster.counts[-c(1:20000)])/180000



###############
##1 cluster
p1<-(0.2*0.2+0.8*0.8)*1/2*1/2

##2 cluster
p2<-(0.2*0.2+0.8*0.8+0.2*0.8*2)*1/4*1/2

 c(p1,p2)/sum(c(p1,p2))

###############
##1 cluster
p1<-(0.2*0.2*0.4+0.8*0.8*0.6)*1/2*1/2

##2 cluster
p2<-(0.2*0.2*0.4*0.4+0.8*0.8*0.6*0.6+0.2*0.8*2*0.4*0.6)*1/4*1/2

1/(0.2*0.2*0.4*0.4*0.25+
0.8*0.8*0.6*0.6*0.25+
0.2*0.8*2*0.4*0.6*0.25)

 c(p1,p2)/sum(c(p1,p2))

2.7551*(0.2*0.2*0.4*0.4*1+
0.8*0.8*0.6*0.6*2+
0.2*0.8*2*0.4*0.6*3)
0.2*0.4
0.8*0.6

0.5^2/2*0.016*5+0.384*5*(1^2-0.5^2)/2
1/7*(0.25/2)+6/7*(1^2/2-0.5^2/2)

###############
##1 cluster
p1<-(0.2^3+0.8^3)*1/2*1/3

##2 cluster
p2<-(0.2^3+0.8^3+0.2*0.8^2+0.2^2*0.8)*1/4*1/2

##3 cluster
p3<-(0.2^3+0.8^3+0.2*0.8^2*3+0.2^2*0.8*3)*1/8*1/6

 c(p1,p2,p3)/sum(c(p1,p2,p3))
sum( c(p1,p2,p3)/sum(c(p1,p2,p3))*c(1:3))

###############
##1 cluster
p1.1<-(0.2^3)*0.4
p1.2<-(0.8^3)*0.6
p1<-sum(c(p1.1,p1.2)*1/3)

##2 cluster
p2.1<-0.2^3*0.4*0.4
p2.2<-0.8^3*0.6*0.6
p2.3<-0.2^2*0.8*0.4*0.6
p2.4<-0.8^2*0.2*0.4*0.6
p2<-sum(c(p2.1,p2.2,p2.3,p2.4)*1/2)

##3 cluster
p3.1<-0.2^3*0.4^3
p3.2<-0.8^3*0.6^3
p3.3<-0.2*0.8^2*0.4*0.6^2*3
p3.4<-0.2^2*0.8*0.4^2*0.6*3
p3<-sum(c(p3.1,p3.2,p3.3,p3.4)*1/6)

p<-c(p1,p2,p3)/sum(c(p1,p2,p3))

sum(p*c(1:3))



