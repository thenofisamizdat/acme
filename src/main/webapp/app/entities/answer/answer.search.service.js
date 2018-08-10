(function() {
    'use strict';

    angular
        .module('acmeApp')
        .factory('AnswerSearch', AnswerSearch);

    AnswerSearch.$inject = ['$resource'];

    function AnswerSearch($resource) {
        var resourceUrl =  'api/_search/answers/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
