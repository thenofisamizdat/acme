(function() {
    'use strict';
    angular
        .module('acmeApp')
        .factory('AnswerMetaData', AnswerMetaData);

    AnswerMetaData.$inject = ['$resource'];

    function AnswerMetaData ($resource) {
        var resourceUrl =  'api/answer-meta-data/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
