# -*- coding=utf-8 -*-
import sys
import datetime
import numpy as np

def readFile(file_name):
	lines = []
	f=open(file_name, 'r')
	lines = f.readlines()
	f.close()
	return lines

def getRating(file_name):    # [[userid1, movieid1, rating1], [userid2, movieid2, rating2]....]
	lines = readFile(file_name)
	ratings = []
	for line in lines:
		rate = line.split("\t")
		ratings.append([int(rate[0]), int(rate[1]), int(rate[2])])
	return ratings

	
	
def getMovie(file_name):  # movieid->moviename
	lines = readFile(file_name)
	movies = {}
	for line in lines:
		movie = line.split("|")
		movies[int(movie[0])] = movie[1]
	return movies

def getUser(file_name):  # [userid1, userid2....]
	lines = readFile(file_name)
	users = []
	for line in lines:
		user = line.split("|")
		users.append(int(user[0]))
	return users
	

def createDictionary(ratings):  #userid->[movieid1, movieid2....]
	user_dic={}
	for i in ratings:
		if i[0] in user_dic:
			user_dic[i[0]].append(i[1])
		else: 
			user_dic[i[0]] = [i[1]]
	
	return user_dic
		

def createColMatrix(user_dic, numOfMovies):   #
	colMatrix = np.zeros((numOfMovies, numOfMovies))
#	colMatrix = [[0 for i in range(numOfMovies)] for j in range(numOfMovies)]
	for key in user_dic:
		movies = user_dic[key]
		for i in movies:
			for j in movies:
				colMatrix[i-1][j-1] += 1
				colMatrix[j-1][i-1] += 1
		
	return colMatrix
				
			
def createUserRating(ratings, numOfMovies, numOfUsers):
	userRating = np.zeros((numOfMovies, numOfUsers))

	for i in ratings:	
		userRating[i[1]-1][i[0]-1] = i[2]
	
	return userRating

			
	
# def recommendByItemFC(colMatrix, ratings, userid, numOfMovies):
	
	# userRating = createUserRating(ratings, userid, numOfMovies)
	
	# recommendation = []
	# for i in range(numOfMovies):
		# score = 0
		# for j in range(numOfMovies):
			# score += colMatrix[i][j]*userRating[j]
		# recommendation.append([score, i])
	#print recommendation
	# recommendation.sort(reverse=True)
	
	# return recommendation
	

def getKey(item):
	return item[1]
	
	
#主程序
if __name__ == '__main__':

	starttime = datetime.datetime.now()

	ratings = getRating(sys.argv[1])  #userid, movieid, rating
	movies = getMovie(sys.argv[2]) #movieid->moviename
	users = getUser(sys.argv[3])#userid
	n = 2; #set the number of recommended items
	
	numOfMovies = len(movies)
	numOfusers = len(users)
	user_dic = createDictionary(ratings)
	colMatrix = createColMatrix(user_dic, numOfMovies)
	userRatingMatrix = createUserRating(ratings, numOfMovies, numOfusers)
#	print userRatingMatrix
	
	recommend_list = np.dot(colMatrix, userRatingMatrix)
	
	for i in range(numOfusers):
		seq = list(enumerate(recommend_list[:,i], start=1))
		# print seq
		# break
		score= sorted(seq, key=getKey, reverse=True)
		print 'For user %d, recommended movies are:\t' %(i+1)
		count = 0
		for j in score:
			if j[0] in user_dic[i+1]:
				continue
			print '%s\t' %(movies[j[0]])
			count += 1
			if(count == n):
				break
			
	
	
	
	# recommend_list={}
	# for user in users:
		# recommend_list[user]=recommendByItemFC(colMatrix, ratings, user, numOfMovies)
	#	print 'For user %d, recommended movies are:\t' %user
		# count = 0
		# for i in recommend_list[user]:
			# if i[1] in user_dic[user]:
				# continue
	#		print '%s\t' %(movies[i[1]+1])
			# count += 1
			# if count == n:
				# break
	#	print '*'*20
	
	endtime = datetime.datetime.now()
	interval=(endtime - starttime).seconds
	print 'The total computation time is: %d seconds' %interval
	
			
			
		
		

