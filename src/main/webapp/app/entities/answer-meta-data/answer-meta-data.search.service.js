(function() {
    'use strict';

    angular
        .module('acmeApp')
        .factory('AnswerMetaDataSearch', AnswerMetaDataSearch);

    AnswerMetaDataSearch.$inject = ['$resource'];

    function AnswerMetaDataSearch($resource) {
        var resourceUrl =  'api/_search/answer-meta-data/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
