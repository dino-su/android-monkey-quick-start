#!/usr/bin/env python

import re
import sys
from junit_xml import TestSuite, TestCase


class MonkeyParser:

    def __init__(self, file):
        self.monkey_log = file
        self.event_count = 0
        self.failure_msg = ""
        self.is_success = False

        self.parse(self.monkey_log)

    def parse(self, file):
        with open(file, 'r') as f:
            for line in f:

                # parse event count
                match = re.match('^Events injected\:\s', line)
                if(match):
                    self.event_count = line[match.end():]

                # parse failure message
                match = re.search('System appears to have crashed at event', line)
                if(match):
                    self.failure_msg = line.rstrip()

                # parse test status
                match = re.search('Monkey finished', line)
                if(match):
                    self.is_success = True

    def show(self):
        measurement_data = "<measurement><name>Events</name><value>%s</value></measurement>" % (self.event_count)
        monkey_test = TestCase('monkey', stdout=measurement_data)

        # handle failure msg
        if(self.is_success is False):
            monkey_test.add_failure_info(self.failure_msg)

        ts = TestSuite("com.skysoft.kkbox.android", [monkey_test])

        # pretty printing is on by default but can be disabled using prettyprint=False
        junit_data = TestSuite.to_xml_string([ts])

        print(junit_data)


def main():
    if len(sys.argv) > 1:
        monkey_log = sys.argv[1]
        MonkeyParser(monkey_log).show()
    else:
        print 'usage: $ python ' + sys.argv[0] + ' monkey.log'

if __name__ == '__main__':
    main()
