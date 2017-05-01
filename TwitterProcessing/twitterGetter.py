
    # There are different kinds of streams: public stream, user stream, multi-user streams
    # In this example follow public stream and set filter as English tweets in Melbourne area
    # For more details refer to https://dev.twitter.com/docs/streaming-apis
import tweepy
import json
#keys get from twitter API auth
consumer_key ='3beGJYN8pfzWokY3NfR6cKWbg'
consumer_secret ='2zrL794Ym71byeNJo3g3l2MnWP2efaLjgKoVrhkML6i6EIICoc'
access_token = '852422975399776256-2mXf0lHyfmoDUkaPQx7ymY58CbtQA7b'
access_token_secret = '987TKNOZQ0YNDmFzsGE8z2B70xaTvUiXLWYsdagszHos1'
#Location coordinates:
GEOBOX_MELBOURNE = [144.7000000000, -48.100000, 155.700000000, -38.100000]
# This is the listener, resposible for receiving data
class StdOutListener(tweepy.StreamListener):
    #init counter to control the number of tweets we want to get
    def __init__(self, api=None):
        super(StdOutListener, self).__init__()
        self.num_tweets = 0
    #write data into a json file, need to replace with database input
    def on_data(self, data):
        json_file = open('data500.json','a')
        self.num_tweets += 1
        print(self.num_tweets)

        if self.num_tweets <= 500:
            json_file.writelines(data)
                     
            return True
        else:
            json_file.close()
            return False
        
    def on_error(self, status_code):
        if status_code == 420:
            return False

if __name__ == '__main__':
    myStreamListener = StdOutListener()
    auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_token, access_token_secret)
    api = tweepy.API(auth)

    print ("Showing all new tweets :")

    stream = tweepy.Stream(auth, myStreamListener)
    stream.filter(locations=GEOBOX_MELBOURNE,languages=["en"])

