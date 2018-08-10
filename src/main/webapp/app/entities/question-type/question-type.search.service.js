(function() {
    'use strict';

    angular
        .module('acmeApp')
        .factory('QuestionTypeSearch', QuestionTypeSearch);

    QuestionTypeSearch.$inject = ['$resource'];

    function QuestionTypeSearch($resource) {
        var resourceUrl =  'api/_search/question-types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
