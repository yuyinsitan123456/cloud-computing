import boto
from boto.ec2.connection import EC2Connection
from boto.ec2.regioninfo import *
#import boto for EC2 connection
region = RegionInfo(name="NeCTAR", endpoint="nova.rc.nectar.org.au")
#choose region for NeCTAR Research Cloud
EC2_ACCESS_KEY="xxx"
EC2_SECRET_KEY="xxx"
#EC2 credentials obtains from NeCTAR dashboard Access and Security
connection = boto.connect_ec2(aws_access_key_id=EC2_ACCESS_KEY,
                    aws_secret_access_key=EC2_SECRET_KEY,
                    is_secure=True,
                    region=region,
                    validate_certs=False,
                    port=8773,
                    path="/services/Cloud")
#make connection to NeCTAR research cloud
connection.create_security_group('botoCreate', 'testing group', vpc_id=None, dry_run=False)
connection.authorize_security_group(group_name='botoCreate', 
	                          src_security_group_name=None, 
	                          src_security_group_owner_id=None, 
	                          ip_protocol='tcp', 
	                          from_port=1, 
	                          to_port=65535, 
	                          cidr_ip='0.0.0.0/0', 
	                          group_id=None, 
	                          src_security_group_group_id=None, 
	                          dry_run=False)
# create security group as required 
instance = connection.run_instances('ami-c163b887', 
	                      instance_type='m2.small',
	                      key_name='testing',
	                      placement='melbourne',
	                      security_groups=['botoCreate'])
instance.instances[0].add_tag("Name","{{boto}}")
#create new instance
vol_req = connection.create_volume(60, 'melbourne-np')
curr_vol = connection.get_all_volumes([vol_req.id])[0]
connection.attach_volume ('vol-3f246727', 'i-2e87a33a', "/dev/vdc")
#create volume and attach to instance