# clj-graphql

- [part 1: hello graphql](doc/hello.md)
- [part 2: using component](doc/component.md)
- [part 3: queries](https://github.com/quan-nh/clj-graphql/commit/00acf0391ff8a04eb9ccddb1c3f0e5ab610a85b3)
- [part 4: db](https://github.com/quan-nh/clj-graphql/commit/e4bbed3737c8392622b0c9531cd4ce9cafd5e53a)
- [part 5: mutations](https://github.com/quan-nh/clj-graphql/commit/e7fdd01a49ad793d8a76293f6912fd538e41bb95)
- [part 6: subscription](doc/subscription.md)
- [part 7: auth](doc/auth.md)

## Setup 

    $ docker-compose up -d
    $ bin/setup-db.sh
    
## Development

    $ lein repl
    user=> (start)
    
The GraphQL endpoint will be at http://localhost:8888/api
and the GraphIQL client will be at http://localhost:8888/ide.     

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2020 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
