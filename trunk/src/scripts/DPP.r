alpha=0.5
K=3
K.counts=c(5,2,1)

logp<-log(alpha^K*prod(gamma(K.counts))/prod(c(0:7)+alpha)*
prod(dnorm(c(-0.3,-0.1,0.5),mean=0,sd=3)))

cov.mat<-matrix(c(1,0.2,0.05,0.2,1.2,0.1,0.05,0.1,0.8),ncol=3,byrow=T)


logp2<-log(alpha^K*prod(gamma(K.counts))/prod(c(0:7)+alpha)*
prod(dnorm(c(-0.3,0.2,-0.1,-0.1,0.03,0.15,0.5,0.4,-0.01),mean=0,sd=3)))

print(logp2,digits=12)

x.mat<-matrix(c(-0.3,0.2,-0.1,-0.1,0.03,0.15,0.5,0.4,-0.01),ncol=3,byrow=T)

logp3<-log(alpha^K*prod(gamma(K.counts))/prod(c(0:7)+alpha)*
prod(dmvnorm(x.mat,mean=c(0.04,-0.01,0.1),sigma=cov.mat)))
print(logp3,digits=12)


