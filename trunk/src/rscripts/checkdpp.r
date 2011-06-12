#testDPP_1.log
log.df<-read.table(file.choose(),header=T)

log.prior<-log(c(1/3,1/6,1/6))
sample.count<-nrow(log.df)
names(log.df)
for(i in 1:sample.count){
	cluster<-log.df[i,]$dpValuable
	if(log.df[i,]$prior-log.prior[cluster]>1e-10){
		stop("Error")
	}

}


#testDPP_2.log
dpp2.df<-read.table(file.choose(),header=T)
log.prior<-log(c(1/3,1/6,1/6))
sample.count<-nrow(dpp2.df)

for(i in 1:sample.count){
	x<-c(dpp2.df[i,]$pointer.0.0,
		dpp2.df[i,]$pointer.1.0,
		dpp2.df[i,]$pointer.2.0)
	u.x<-unique(x)
	
	cluster<-dpp2.df[i,]$dpValuable
	log.prior.prob=log.prior[cluster]+sum(log(dnorm(u.x,sd=0.3)))
	if((dpp2.df[i,]$prior-log.prior.prob)>1e-10){
		stop("Error")
	}

}
