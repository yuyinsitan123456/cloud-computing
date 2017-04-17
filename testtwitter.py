import tweepy

#override tweepy.StreamListener to add logic to on_status
consumer_key ='3beGJYN8pfzWokY3NfR6cKWbg'
consumer_secret ='2zrL794Ym71byeNJo3g3l2MnWP2efaLjgKoVrhkML6i6EIICoc'
access_token = '852422975399776256-2mXf0lHyfmoDUkaPQx7ymY58CbtQA7b'
access_token_secret = '987TKNOZQ0YNDmFzsGE8z2B70xaTvUiXLWYsdagszHos1'
# This is the listener, resposible for receiving data
class StdOutListener(tweepy.StreamListener):
    def on_data(self, data):
        # Twitter returns data in JSON format - we need to decode it first
        decoded = json.loads(data)

        # Also, we convert UTF-8 to ASCII ignoring all bad characters sent by users
        #print ('@%s: %s' % (decoded['user']['screen_name'], decoded['text'].encode('ascii', 'ignore')))
        print（data）
        #print(decoded)
        print (' ')
        return True

    def on_error(self, status_code):
        if status_code == 420:
            return False

if __name__ == '__main__':
    myStreamListener = StdOutListener()
    auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
    auth.set_access_token(access_token, access_token_secret)
    api = tweepy.API(auth)

    print ("Showing all new tweets for #programming:")

    # There are different kinds of streams: public stream, user stream, multi-user streams
    # In this example follow #programming tag
    # For more details refer to https://dev.twitter.com/docs/streaming-apis
    stream = tweepy.Stream(auth, myStreamListener)
    stream.filter(track=['food'])
